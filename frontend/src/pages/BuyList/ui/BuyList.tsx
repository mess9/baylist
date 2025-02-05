import {
  createEffect,
  createResource,
  type Component,
  type Setter,
} from "solid-js";
import type { SortableEvent } from "solid-sortablejs";
import Sortable from "solid-sortablejs";

import type { ICategory } from "/widgets/Category/ui/Category";
import Category from "/widgets/Category/ui/Category";

import classes from "./BuyList.module.css";
import { createStore } from "solid-js/store";
import type { IItem } from "/features/Item/ui/Item";

import { Show } from "solid-js";
import {
  fetchCategories,
  updateCategoriesOrder,
  updateItemsOrder,
  moveItem,
} from "../api/buyListService";

const BuyList: Component = () => {
  const [categories, setCategories] = createStore<ICategory[]>([]);

  const [getCategories, { mutate }] = createResource<ICategory[]>(
    fetchCategories,
    {
      initialValue: [],
    }
  );

  createEffect(() => {
    setCategories(getCategories() || []);
    mutate(categories);
  });

  const createSetItemsForCategory = (parentId: string): Setter<IItem[]> => {
    return (updater) => {
      console.log(updater, "updater");
      setCategories(
        (category) => category.id === parentId,
        "items",
        typeof updater === "function" ? updater : () => updater
      );
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
    };

    const updateItemOrder = (parentId: string, withUpdate: boolean) => {
      let updatedItems: IItem[] = [];
      setCategories(
        (category) => category.id === parentId,
        "items",
        (items) => {
          updatedItems = items.map((item, index) => ({
            ...item,
            order: index + 1,
          }));
          if (withUpdate) {
            updateItemsOrder(parentId, updatedItems);
          }
          return updatedItems;
        }
      );
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
      updateCategoriesOrder(categories);
      return;
    }

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

    const item = e.item;
    const itemId = item.dataset["id"];
    if (!itemId) return;

    const closestToParent = toList.closest("[data-id]") as HTMLElement | null;
    if (!closestToParent) return;

    const parentToId = closestToParent.dataset["id"];
    if (!parentToId) return;

    updateSectionId(parentToId);
    const newItemsFromList = updateItemOrder(parentFromId, false);
    const newItemsToList = updateItemOrder(parentToId, false);

    moveItem(itemId, parentToId, newItemsFromList, newItemsToList);
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
            onEnd={(e) => handleMove(e, 0)}
          >
            {(category) => (
              <li>
                <Category
                  {...category}
                  setItems={createSetItemsForCategory(category.id)}
                  handleMove={handleMove}
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
