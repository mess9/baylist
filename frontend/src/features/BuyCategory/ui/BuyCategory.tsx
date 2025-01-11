import type { ParentComponent } from "solid-js";
import { children } from "solid-js";

import { IconEllipsisHorizontalSolid, IconPlusSolid } from "/app/assets/svg/icons";

import classes from "./BuyCategory.module.css";

const BuyCategory: ParentComponent = (props) => {
	const c = children(() => props.children);
	return (
		<section >
			<div class={classes["buy-category__header"]}>
				<h2>Section Header</h2>
				<button type="button"><IconPlusSolid /></button>
				<button type="button"><IconEllipsisHorizontalSolid /></button>
			</div>
			{c()}
		</section>
	);
};

export default BuyCategory;
