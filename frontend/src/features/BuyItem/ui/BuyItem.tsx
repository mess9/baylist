import classes from "./BuyItem.module.css";

export default function BuyItem() {
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
