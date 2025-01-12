import type { Component } from "solid-js";

import { IconTrashSolid } from "/app/assets/svg/icons";

import classes from "./BuyItem.module.css";

const BuyItem: Component = () => {
	return (
		<div class={classes["buy-item"]}>
			<input class={classes["buy-item__check"]} type="checkbox" />
			<span class={classes["buy-item__content"]}>ogurec</span>
			<button class={classes["buy-item__remove"]} type="button">
				<IconTrashSolid />
			</button>
		</div>
	);
};

export default BuyItem;
