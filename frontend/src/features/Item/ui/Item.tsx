import type { Component } from "solid-js";

import { IconDnDHandler, IconTrashSolid } from "/app/assets/svg/icons";

import classes from "./Item.module.css";

export interface IItem {
	id: string;
	name: string;
	order: number;
	parentId: string;
}

const Item: Component<IItem> = (props) => {
	return (
		<div class={classes["item"]}>
			<button class={classes["buy-item__dnd-button"]} type="button">
				<IconDnDHandler />
			</button>
			<input class={classes["item__check-input"]} type="checkbox" />
			<span class={classes["item__content-span"]}>{props.name}</span>
			<button class={classes["item__remove-button"]} type="button">
				<IconTrashSolid />
			</button>
		</div>
	);
};

export default Item;
