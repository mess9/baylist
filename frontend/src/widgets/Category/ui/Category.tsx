import type { Component, Setter } from "solid-js";
import { createEffect, createSignal, mergeProps, on } from "solid-js";

import { clickAway } from "/src/shared/directives/clickAway";

import {
  IconEllipsisHorizontalSolid,
  IconEnterOutline,
  IconPlusSolid,
} from "/app/assets/svg/icons";

import type { Item as ItemType } from "/shared/api/types/syncEntities";
import Item from "/features/Item/ui/Item";

import type { SortableEvent } from "solid-sortablejs";
import Sortable from "solid-sortablejs";

import type { Section } from "/shared/api/types/syncEntities";

import classes from "./Category.module.css";
import classesItem from "/features/Item/ui/Item.module.css";
import { NoCategoryType } from "/src/pages/BuyList/api/buyListService";

export interface ICategory extends Section {
  items: ItemType[];
  delimiter?: "top" | "bottom";
}

type ICategoryProps = (ICategory | NoCategoryType) & {
  setItems: Setter<ItemType[]>;
  handleMove: (event: SortableEvent, depth: number) => void;
  handleAddItem: (content: string) => void;
  handleEditItem: (itemId: string) => (content: string) => void;
  handleRemoveItem: (itemId: string) => () => void;
  handleCollapseCategory: (collapsed: boolean) => void;
  isLoadingCollapsed: boolean | string;
  isLoadingOuter: boolean | string;
}

const Category: Component<ICategoryProps> = (props) => {
  const [isCollapsed, setIsCollapsed] = createSignal(true);
  const [isAddItemVisible, setAddItemVisible] = createSignal(false);
  const [inputValue, setInputValue] = createSignal("");
  const [isLoading, setIsLoading] = createSignal(false);
  const [addItemBeenCalled, setAddItemBeenCalled] = createSignal(false);

  let ulRef: HTMLUListElement | undefined;

  const merge = mergeProps({ delimiter: "bottom" }, props);

  //TODO: МЕМОИЗИРОВАТЬ В ЗАВИСИМОСТИ ОТ КОНТЕНТА?
  const changeMaxHeight = () => {
    if (!ulRef) return;
    const ul = ulRef;
    requestAnimationFrame(() => {
		const currentHeight = ul.scrollHeight;
		ul.style.maxHeight = isCollapsed() ? "0" : `${currentHeight}px`;
  	})
  };

  const scrollToLastItem = () => {
    if (!ulRef) return;

    const sortableList = ulRef.querySelector(
      ".sortablejs",
    ) as HTMLElement | null;
    if (!sortableList) return;

    const lastItem = sortableList.lastElementChild as HTMLElement | null;
    if (!lastItem) return;

    const lastItemRect = lastItem.getBoundingClientRect();
    const viewportHeight = window.innerHeight;
    const currentScrollY = window.scrollY;

    const lastItemDocumentBottom = lastItemRect.bottom + currentScrollY;
    const desiredDocumentBottom =
      currentScrollY + viewportHeight - lastItemRect.height * 2;
    const overflowAmount = lastItemDocumentBottom - desiredDocumentBottom;

    if (lastItemRect.bottom > viewportHeight) {
      const desiredScrollTop = currentScrollY + overflowAmount;
      const maxScrollTop =
        document.documentElement.scrollHeight - viewportHeight;
      const finalScrollTop =
        desiredScrollTop > maxScrollTop ? maxScrollTop : desiredScrollTop;

      window.scrollTo({
        top: finalScrollTop,
        behavior: "smooth",
      });
    }
  };

  const toggleVisibility = () => {
    if (!ulRef) return;

    merge.handleCollapseCategory(!isCollapsed());
    changeMaxHeight();
  };

  const toggleAddItem = () => {
    setAddItemVisible(!isAddItemVisible());
  };

  const onAddItem = () => {
    setIsLoading(true);
    merge.handleAddItem(inputValue());
    setInputValue("");
    setAddItemVisible(false);
    setAddItemBeenCalled(true);
  };

  createEffect(() => {
    setIsCollapsed(merge.collapsed);
  });

  createEffect(
    on([isCollapsed, () => merge.items], () => {
      changeMaxHeight();
      setIsLoading(false);
    }),
  );

  createEffect(
    on([isLoading], () => {
      if (!isLoading() && addItemBeenCalled()) {
        scrollToLastItem();
      }
    }),
  );

  return (
    <section
      classList={{
        [classes["category-section"]]: true,
        [classes["category-section--no-category"]]: merge.id === "no_category",
      }}
    >
      <div
        classList={{
          [classes["category__header"]]: true,
          [classes["category__header--no-category"]]:
            merge.id === "no_category",
          [classes["category__item-li--delimiter-bottom"]]:
            merge.id === "no_category",
        }}
      >
        <label
          classList={{
            [classes["category__drop-down-control-label"]]: true,
            [classes["category__drop-down-control-label--disabled"]]:
              merge.isLoadingCollapsed === merge.id,
          }}
        >
          <div
            classList={{
              [classes["on-load-mask"]]: true,
              [classes["on-load-mask--hidden"]]: !isLoading(),
            }}
          />
          <input
            class={classes["category__drop-down-checkbox"]}
            type="checkbox"
            checked={merge.collapsed}
            // disabled={merge.isLoadingCollapsed === merge.id}
            onChange={toggleVisibility}
          />
          <h2>{merge.name || "Out of categories"}</h2>

          {/* {merge.id} */}
        </label>
        <div class={classes["category__controls"]}>
          <button
            class={classes["category__add-item-button"]}
            type="button"
            onClick={toggleAddItem}
          >
            <IconPlusSolid />
          </button>
          <button class={classes["category__more-button"]} type="button">
            <IconEllipsisHorizontalSolid />
          </button>
        </div>
      </div>
      <div
        use:clickAway={() => setAddItemVisible(false)}
        classList={{
          [classes["category__add-item"]]: true,
          [classes["category__add-item--expanded"]]: isAddItemVisible(),
        }}
      >
        <input
          class={classes["category__add-item-input"]}
          type="text"
          placeholder="Add item"
          value={inputValue()}
          onInput={(e) => setInputValue(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && onAddItem()}
          enterkeyhint={"enter"}
        />
        <button
          class={classes["category__add-item-button"]}
          type="button"
          onClick={onAddItem}
        >
          <IconEnterOutline />
        </button>
      </div>
      <ul
        classList={{
          [classes["category-ul"]]: true,
        }}
        ref={ulRef}
      >
        <Sortable
          idField={"id"}
          items={merge.items || []}
          setItems={merge.setItems}
          group="bl1"
          handle={`.${classesItem["buy-item__dnd-button"]}`}
          onChange={() => changeMaxHeight()}
          onEnd={(e) => merge.handleMove(e, 1)}
        >
          {(item) => (
            <li
              classList={{
                [classes["category__item-li--last"]]:
                  item.child_order ===
                  merge.items.reduce((max, item) => {
                    return item.child_order > max ? item.child_order : max;
                  }, 0),

                [classes["category__item-li--delimiter-top"]]:
                  merge.delimiter === "top",
                [classes["category__item-li--delimiter-bottom"]]:
                  merge.delimiter === "bottom",
              }}
            >
              <Item
                {...item}
                handleEditItem={merge.handleEditItem(item.id)}
                handleRemoveItem={merge.handleRemoveItem(item.id)}
                isLoading={merge.isLoadingOuter}
              />
            </li>
          )}
        </Sortable>
      </ul>
    </section>
  );
};

export default Category;
