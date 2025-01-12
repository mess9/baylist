import type { Component } from "solid-js";
import { For } from "solid-js";
import BuyItem from "/features/BuyItem";
import classes from "./BuyList.module.css";
import BuyCategory from "/features/BuyCategory/ui/BuyCategory";

const BuyList: Component<{ delimiter: "top" | "bottom" }> = (props) => {
	return (
		<ul class={classes["buy-list"]}>
			<For each={[1, 2, 3, 4]}>
				{() => (
					<BuyCategory>
						<For each={[1, 2, 3, 4, 5]}>
							{() => (
								<li
									classList={{
										[classes["buy-list__li"]]: true,
										[classes[
											"buy-list__li--delimeter-top"
										]]: props.delimiter === "top",
										[classes[
											"buy-list__li--delimeter-bottom"
										]]: props.delimiter === "bottom",
									}}
								>
									<BuyItem />
								</li>
							)}
						</For>
					</BuyCategory>
				)}
			</For>
		</ul>
	);
};

export default BuyList;
