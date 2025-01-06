import { /*Component*/ For } from "solid-js";
import BuyItem from "/features/BuyItem";
import classes from "./BuyList.module.css";

export default function BuyList(props: {delimiter: 'top' | 'bottom'}) {
	return (
		<ul>
			<For each={[1,2,3]}>
				{
					(item, i) => (
						<li classList={{
							[classes["buy-list--delimeter-top"]]: props.delimiter === "top",
							[classes["buy-list--delimeter-bottom"]]: props.delimiter === "bottom"
						}}>
							<BuyItem /> {`item: ${item} index: ${i()}`}
						</li>
					)
				}
			</For>
		</ul>
	)
}
