import {
  createEffect,
  createSignal,
  Match,
  onMount,
  Switch,
  type Component,
} from "solid-js";

import {
  IconDnDHandler,
  IconPencilSquareOutline,
  IconTrashSolid,
  IconXMarkOutline,
} from "/app/assets/svg/icons";

import classes from "./Item.module.css";
import type { Item as ItemType } from "/shared/api/types/syncEntities";

type ItemProps = ItemType & {
  handleEditItem: (content: string) => void;
  handleRemoveItem: () => void;
  isLoading: boolean | string;
};

const Item: Component<ItemProps> = (props) => {
  const [itemContent, setItemContent] = createSignal("");
  const [isEditMode, setIsEditMode] = createSignal(false);
  const [editedContent, setEditedContent] = createSignal("");
  const [isFreshItem, setIsFreshItem] = createSignal(false);
  const [inputRef, setInputRef] = createSignal<HTMLInputElement | undefined>();

  onMount(() => {
    setItemContent(props.content);
  });

  createEffect(() => {
    setEditedContent(props.content);
    if (isEditMode()) {
      inputRef()?.focus();
    }
  });

  createEffect(() => {
    if (props.added_at && Date.parse(props.added_at) > Date.now() - 10000) {
      setIsFreshItem(true);
      setTimeout(() => {
        setIsFreshItem(false);
      }, 1000);
    }
  });

  const handleEditInputBlur = () => {
    props.handleEditItem(editedContent());
    setIsEditMode(false);
  };

  return (
    <div
      classList={{
        [classes["item"]]: true,
        [classes["item--fresh"]]: isFreshItem(),
      }}
    >
      <Switch>
        <Match when={isEditMode()}>
          <input
            class={classes["item__edit-input"]}
            ref={setInputRef}
            type="text"
            value={editedContent()}
            onInput={(e) => {
              setEditedContent(e.target.value);
              setItemContent(e.target.value);
            }}
            onBlur={handleEditInputBlur}
          />
          <button class={classes["item__close-edit-button"]} type="button">
            <IconXMarkOutline />
          </button>
        </Match>
        <Match when={!isEditMode()}>
          <div class={classes["item__content"]}>
            <button class={classes["buy-item__dnd-button"]} type="button">
              <IconDnDHandler />
            </button>
            <input class={classes["item__check-input"]} type="checkbox" />
            <span
              classList={{
                [classes["item__content-span"]]: true,
                [classes["item__content-span--loading"]]:
                  props.isLoading === props.id,
              }}
            >
              {itemContent()}
            </span>
            <button
              class={classes["item__edit-button"]}
              type="button"
              onClick={() => setIsEditMode(true)}
            >
              <IconPencilSquareOutline />
            </button>
            <button
              class={classes["item__remove-button"]}
              type="button"
              onClick={() => props.handleRemoveItem()}
            >
              <IconTrashSolid />
            </button>
          </div>
        </Match>
      </Switch>
    </div>
  );
};

export default Item;
