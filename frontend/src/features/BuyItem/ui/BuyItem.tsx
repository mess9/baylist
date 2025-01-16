import type { Component, VoidComponent } from "solid-js";

import { IconDnDHandler, IconTrashSolid } from "/app/assets/svg/icons";

import {
	createSortable,
	maybeTransformStyle,
	type Id,
} from "@thisbeyond/solid-dnd";

import classes from "./BuyItem.module.css";

export interface IBuyItem {
	id: Id;
	name: string;
	type: "buyItem";
	order: string;
	buyCategory: Id;
}

export const BuyItemOverlay: VoidComponent<{ name: string }> = (props) => {
	return (
		<div classList={{ [classes["buy-item"]]: true }}>
			<button class={classes["buy-item__dnd-button"]} type="button">
				<IconDnDHandler />
			</button>
			<input class={classes["buy-item__check-input"]} type="checkbox" />
			<span class={classes["buy-item__content-span"]}>{props.name}</span>
			<button class={classes["buy-item__remove-button"]} type="button">
				<IconTrashSolid />
			</button>
		</div>
	);
};

const BuyItem: Component<IBuyItem> = (props) => {
	const sortable = createSortable(props.id, {
		type: "buyItem",
		buyCategory: props.buyCategory,
	});

	return (
		<div
			ref={sortable.ref}
			style={maybeTransformStyle(sortable.transform)}
			classList={{ [classes["buy-item"]]: true }}
		>
			<button
				{...sortable.dragActivators}
				class={classes["buy-item__dnd-button"]}
				type="button"
			>
				<IconDnDHandler />
			</button>
			<input class={classes["buy-item__check-input"]} type="checkbox" />
			<span class={classes["buy-item__content-span"]}>{props.name}</span>
			<button class={classes["buy-item__remove-button"]} type="button">
				<IconTrashSolid />
			</button>
		</div>
	);
};

export default BuyItem;
