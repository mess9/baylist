import type { Component } from "solid-js";
import { For } from "solid-js";
import BuyItem from "/features/BuyItem";
import classes from "./BuyList.module.css";
import BuyCategory from "/features/BuyCategory/ui/BuyCategory";

const BuyList: Component<{delimiter: 'top' | 'bottom'}> = (props) => {
	return (
		<ul>
			<For each={[1,2,3]}>
				{
					() => (
						<BuyCategory>
							<For each={[1,2,3]}>
								{
									(item, i) => (
										<li classList={{
											[classes["buy-list--delimeter-top"]]: props.delimiter === "top",
											[classes["buy-list--delimeter-bottom"]]: props.delimiter === "bottom"
										}}>
											<BuyItem />
										</li>
									)
								}
							</For>
						</BuyCategory>
					)
				}
			</For>
		</ul>
	)
};

export default BuyList;
