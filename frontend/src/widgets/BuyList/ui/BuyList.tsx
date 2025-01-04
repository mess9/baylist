import { For } from "solid-js";
import BuyItem from "/features/BuyItem";

export default function BuyList() {
	return (
		<ul>
			<For each={[1,2,3]}>
				{
					(item, i) => (
						<li>
							<BuyItem /> {`item: ${item} index: ${i()}`}
						</li>
					)
				}
			</For>
		</ul>
	)
}
