import type { Component } from "solid-js";
import classes from "./BuyItem.module.css";

const BuyItem: Component = () => {
	console.log("Child rendered"); // Логируем вызов
	return (
		<div class={classes["buy-item"]}>
			<label class={classes["buy-item__label"]}>	
				<input type="checkbox" />
				<span>ogurec</span>
			</label>
			<button type="button">Remove</button>
		</div>
	);
}

export default BuyItem;
