import {
  createEffect,
  createResource,
  createSignal,
  on,
  type Component,
  type Setter,
} from "solid-js";
import type { SortableEvent } from "solid-sortablejs";
import Sortable from "solid-sortablejs";

import type { ICategory } from "/widgets/Category/ui/Category";
import Category from "/widgets/Category/ui/Category";

import { createStore } from "solid-js/store";
import type { Item as ItemType, Project } from "/shared/api/types/syncEntities";

import { Show, onMount } from "solid-js";
import {
  fetchCategoriesWithItems,
  updateCategoriesOrder,
  updateItemsOrder,
  moveItem,
  addItem,
  sync,
  updateItem,
  deleteItem,
  fetchProjects,
  updateCategoryCollapsed,
} from "../api/buyListService";

import classes from "./BuyList.module.css";
import classesCategory from "/widgets/Category/ui/Category.module.css";
const BuyList: Component = () => {
  const [isLoading, setIsLoading] = createSignal<boolean | string>(false);
  const [isLoadingCollapsed, setIsLoadingCollapsed] = createSignal<
    boolean | string
  >(false);
  const [categories, setCategories] = createStore<ICategory[]>([]);
  const [project, setProject] = createSignal<Project[] | []>([]);

  onMount(() => {
    fetchProjects().then((projects) => {
      setProject(projects.filter((project) => project.name === "Test"));
    });
  });

  const [getCategories, { mutate, refetch }] = createResource<ICategory[]>(
    () => (project().length ? fetchCategoriesWithItems(project()[0].id) : []),
    { initialValue: [] }
  );

  createEffect(
    on(
      () => project().length,
      () => {
        refetch();
      }
    )
  );

  createEffect(() => {
    setCategories(getCategories() || []);
    mutate(categories);
  });

  const createSetItemsForCategory = (parentId: string): Setter<ItemType[]> => {
    return (updater) => {
      setCategories(
        (category) => category.id === parentId,
        "items",
        typeof updater === "function" ? updater : () => updater
      );
    };
  };

  const handleAddItem = (sectionId: string, projectId: string) => {
    return (content: string) => {
      sync("", [])
        .then((value) => {
          addItem({
            content,
            section_id: sectionId,
            project_id: projectId,
          }).then(() => {
            sync(value.sync_token, ["items"]).then((valueNew) => {
              valueNew.items?.forEach((item) => {
                setCategories(
                  (category) => category.id === item.section_id,
                  "items",
                  (items) =>
                    [...items, item].sort(
                      (a, b) => a.child_order - b.child_order
                    )
                );
              });
            });
          });
        })

        .catch((err) => err);
    };
  };

  const handleEditItem = (itemId: string) => {
    return (content: string) => {
      setIsLoading(itemId);
      sync("", []).then((value) => {
        updateItem({
          id: itemId,
          content,
        }).then(() => {
          sync(value.sync_token, ["items"]).then((valueNew) => {
            valueNew.items?.forEach((item) => {
              setCategories(
                (category) => category.id === item.section_id,
                "items",
                (items) =>
                  items.map((item) =>
                    item.id === itemId ? { ...item, content } : item
                  )
              );
              setIsLoading(false);
            });
          });
        });
      });
    };
  };

  const handleRemoveItem = (categoryId: string) => (itemId: string) => {
    return () => {
      setIsLoading(itemId);
      deleteItem(itemId).then(() => {
        setCategories(
          (category) => category.id === categoryId,
          "items",
          (items) => items.filter((item) => item.id !== itemId)
        );
        setIsLoading(false);
      });
    };
  };

  const handleMove = (e: SortableEvent, depth: number) => {
    const updateCategoryOrder = () => {
      setCategories((categories) =>
        categories.map((category, index) => ({
          ...category,
          order: index + 1,
        }))
      );
      updateCategoriesOrder(categories);
    };

    const updateItemOrder = (
      parentId: string,
      withUpdate: boolean
    ): ItemType[] => {
      const category = categories.find((cat) => cat.id === parentId);
      if (!category) return [];

      const updatedItems = category.items.map((item, index) => ({
        ...item,
        child_order: index + 1,
      }));

      setCategories((cat) => cat.id === parentId, "items", updatedItems);

      if (withUpdate) {
        updateItemsOrder(parentId, updatedItems);
      }

      return updatedItems;
    };

    const updateSectionId = (parentId: string) => {
      setCategories(
        (category) => category.id === parentId,
        "items",
        (items) => items.map((item) => ({ ...item, sectionId: parentId }))
      );
    };

    if (depth === 0) {
      updateCategoryOrder();
      return;
    }

    const item = e.item;

    const itemId = item.dataset["id"];

    if (!itemId) return;

    const fromList = e.from;
    const closestFromParent = fromList.closest(
      "[data-id]"
    ) as HTMLElement | null;
    if (!closestFromParent) return;

    const parentFromId = closestFromParent.dataset["id"];
    if (!parentFromId) return;

    const toList = e.to;

    if (fromList === toList) {
      updateItemOrder(parentFromId, true);
      return;
    }

    const closestToParent = toList.closest("[data-id]") as HTMLElement | null;
    if (!closestToParent) return;

    const parentToId = closestToParent.dataset["id"];
    if (!parentToId) return;

    updateSectionId(parentToId);

    const newItemsFromList = updateItemOrder(parentFromId, false);
    const newItemsToList = updateItemOrder(parentToId, false);

    moveItem(itemId, parentToId, newItemsFromList, newItemsToList);
  };

  const handleCollapseCategory = (categoryId: string) => {
    return (collapsed: boolean) => {
      setIsLoadingCollapsed(categoryId);
      sync("", []).then((value) => {
        updateCategoryCollapsed(categoryId, collapsed).then(() => {
          sync(value.sync_token, ["sections"]).then((valueNew) => {
            valueNew.sections?.forEach((section) => {
              setCategories(
                (category) => category.id === section.id,

                "collapsed",
                section.collapsed
              );
            });
            setIsLoadingCollapsed(false);
          });
        });
      });
    };
  };

  return (
    <div
      class={classes["buy-list-container"]}
      onDragOver={(e) => e.preventDefault()}
    >
      <ul class={classes["buy-list-ul"]}>
        <Show when={categories.length} fallback={<div>GRUZHU</div>}>
          <Sortable
            idField={"id"}
            items={categories}
            setItems={setCategories}
            handle={`.${classesCategory["category__header"]}`}
            onEnd={(e) => handleMove(e, 0)}
            disabled={isLoadingCollapsed() !== false}
          >
            {(category) => (
              <li>
                <Category
                  {...category}
                  setItems={createSetItemsForCategory(category.id)}
                  handleMove={handleMove}
                  handleAddItem={handleAddItem(category.id, project()[0].id)}
                  handleEditItem={handleEditItem}
                  isLoadingOuter={isLoading()}
                  handleRemoveItem={handleRemoveItem(category.id)}
                  handleCollapseCategory={handleCollapseCategory(category.id)}
                  isLoadingCollapsed={isLoadingCollapsed()}
                />
              </li>
            )}
          </Sortable>
        </Show>
      </ul>
    </div>
  );
};

export default BuyList;
