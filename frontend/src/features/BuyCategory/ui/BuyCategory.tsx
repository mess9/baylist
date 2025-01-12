import type { ParentComponent } from "solid-js";
import { children } from "solid-js";

import { IconEllipsisHorizontalSolid, IconPlusSolid } from "/app/assets/svg/icons";

import classes from "./BuyCategory.module.css";

const BuyCategory: ParentComponent = (props) => {
	const c = children(() => props.children);
	return (
		<section class={classes["buy-category"]}>
			<div class={classes["buy-category__header"]}>
				<label class={classes["buy-category__drop-down-control"]}>
					<input class={classes["buy-category__drop-down"]} type="checkbox" />
					<h2>Section Header</h2>
				</label>
				<div class={classes["buy-category__controls"]}>
					<button class={classes["buy-category__add-item"]} type="button"><IconPlusSolid /></button>
					<button class={classes["buy-category__more"]}type="button"><IconEllipsisHorizontalSolid /></button>
				</div>
			</div>
			{c()}
		</section>
	);
};

export default BuyCategory;
