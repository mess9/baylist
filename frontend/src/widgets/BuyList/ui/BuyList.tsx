import type { Component } from "solid-js";
import { For } from "solid-js";

import BuyItem from "/features/BuyItem";
import BuyCategory from "/features/BuyCategory/ui/BuyCategory";

import classes from "./BuyList.module.css";

const BuyList: Component = () => {
	return (
		<ul class={classes["buy-list-ul"]}>
			<For each={[1, 2, 3, 4]}>
				{() => (
					<li>
						<BuyCategory delimiter="bottom">
							<BuyItem />
						</BuyCategory>
					</li>
				)}
			</For>
		</ul>
	);
};

export default BuyList;
