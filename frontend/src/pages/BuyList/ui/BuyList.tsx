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
  const [isLogIn, setIsLogIn] = createSignal("");
  const [isLoading, setIsLoading] = createSignal<boolean | string>(false);
  const [err, setErr] = createSignal("");
  const [isLoadingCollapsed, setIsLoadingCollapsed] = createSignal<
    { id: string; collapsed: boolean }[]
  >([]);
  const [categories, setCategories] = createStore<ICategory[]>([]);
  const [project, setProject] = createSignal<Project[] | []>([]);

  onMount(() => {
    setIsLogIn(
      document.cookie
        .split("; ")
        .find((row) => row.startsWith("username="))
        ?.split("=")[1] || "",
    );
  });

  createEffect(
    on(
      () => isLogIn(),
      () => {
        if (isLogIn().length) {
          document.cookie = `username=${isLogIn()}; max-age=${24 * 60 * 60}; path=/; SameSite=Strict; Secure`;
          fetchProjects()
            .then((projects) => {
              setProject(
                projects.filter((project) => project.name === "buylist"),
              );
            })
            .catch((err) => setErr(`${err}`));
        }
      },
    ),
  );

  const [getCategories, { mutate, refetch }] = createResource<ICategory[]>(
    () =>
      project().length && isLogIn().length
        ? fetchCategoriesWithItems(project()[0].id)
        : [],
    { initialValue: [] },
  );

  createEffect(
    on(
      () => project().length,
      () => {
        refetch();
      },
    ),
  );

  createEffect(() => {
    setErr(getCategories.error);
    setCategories(getCategories() || []);
    mutate(categories);
  });

  const createSetItemsForCategory = (parentId: string): Setter<ItemType[]> => {
    return (
      updater:
        | Exclude<ItemType[], () => void>
        | ((prev: ItemType[]) => ItemType[]),
    ) => {
      setCategories(
        (category) => category.id === parentId,
        "items",
        typeof updater === "function" ? updater : () => updater,
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
                      (a, b) => a.child_order - b.child_order,
                    ),
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
                    item.id === itemId ? { ...item, content } : item,
                  ),
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
          (items) => items.filter((item) => item.id !== itemId),
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
        })),
      );
      updateCategoriesOrder(categories);
    };

    const updateItemOrder = (
      parentId: string,
      withUpdate: boolean,
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
        (items) => items.map((item) => ({ ...item, sectionId: parentId })),
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
      "[data-id]",
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
      setCategories(
        (category) => category.id === categoryId,
        "collapsed",
        collapsed,
      );

      if (isLoadingCollapsed().some((cat) => cat.id === categoryId)) {
        setIsLoadingCollapsed((prev) => [
          ...prev.filter((cat) => cat.id !== categoryId),
          { id: categoryId, collapsed },
        ]);
        return;
      }

      setIsLoadingCollapsed((prev) => [
        ...prev.filter((cat) => cat.id !== categoryId),
        { id: categoryId, collapsed },
      ]);

      sync("", []).then((value) => {
        updateCategoryCollapsed(categoryId, collapsed).then(() => {
          sync(value.sync_token, ["sections"]).then((valueNew) => {
            valueNew.sections?.forEach((section) => {
              const currentisLoadingCollapsed = isLoadingCollapsed().find(
                (cat) => cat.id === categoryId,
              );

              if (
                currentisLoadingCollapsed &&
                section.collapsed !== currentisLoadingCollapsed.collapsed
              ) {
                updateCategoryCollapsed(
                  categoryId,
                  currentisLoadingCollapsed.collapsed,
                );
                return;
              }

              setCategories(
                (category) => category.id === section.id,

                "collapsed",
                section.collapsed,
              );
            });
            setIsLoadingCollapsed((prev) =>
              prev.filter((category) => category.id !== categoryId),
            );
          });
        });
      });
    };
  };

  const handleLogin = (e: KeyboardEvent) => {
    // eslint-disable-next-line @typescript-eslint/no-unused-expressions
    e.key === "Enter" &&
      e.target instanceof HTMLInputElement &&
      setIsLogIn(`${e.target.value}`);
  };

  return (
    <Show
      when={isLogIn()}
      fallback={
        <>
          <input
            type="text"
            class={classes["buy-list__login-input"]}
            enterkeyhint={"enter"}
            onKeyDown={handleLogin}
            placeholder="Input your name, press Enter"
          />
        </>
      }
    >
      <div
        class={classes["buy-list-container"]}
        onDragOver={(e) => e.preventDefault()}
      >
        <ul class={classes["buy-list-ul"]}>
          <Show
            when={categories.length}
            fallback={
              <div class={classes["buy-list__loader"]}>
                <span>GRUZHU...</span>
                {err() && (
                  <>
                    <span>
                      ~||~
                      <span style={{ color: "red" }}>
                        {" "}
                        Try reload, maybe incorrect name{" "}
                      </span>
                      ~||~
                    </span>
                    {err()}
                  </>
                )}
                <button
                  class={classes["buy-list__log-out-button"]}
                  onClick={() => {
                    document.cookie =
                      "username=; max-age=0; path=/; SameSite=Strict; Secure";
                    setIsLogIn("");
                    setErr("");
                  }}
                >
                  LogOut and Reload
                </button>
              </div>
            }
          >
            <Sortable
              idField={"id"}
              items={categories}
              setItems={setCategories}
              handle={`.${classesCategory["category__header"]}`}
              onEnd={(e) => handleMove(e, 0)}
              // disabled={!!isLoadingCollapsed().length}
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
                    isLoadingCollapsed={
                      isLoadingCollapsed().find((cat) => cat.id === category.id)
                        ?.id || false
                    }
                  />
                </li>
              )}
            </Sortable>
          </Show>
        </ul>
      </div>
    </Show>
  );
};

export default BuyList;
