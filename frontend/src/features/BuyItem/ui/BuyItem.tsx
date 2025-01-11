import type { Component } from "solid-js";

import { IconTrashSolid } from "/app/assets/svg/icons";

import classes from "./BuyItem.module.css";

const BuyItem: Component = () => {
	return (
		<div class={classes["buy-item"]}>
			<label class={classes["buy-item__label"]}>	
				<input type="checkbox" />
				<span>ogurec</span>
			</label>
			<button type="button"><IconTrashSolid /></button>
		</div>
	);
}

export default BuyItem;
