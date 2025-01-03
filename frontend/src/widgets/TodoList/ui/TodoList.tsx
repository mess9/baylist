import { For } from "solid-js";
import TodoItem from "/features/TodoItem/ui/TodoItem";

export default function TodoList() {
	return (
		<ul>
			<For each={[1,2,3]}>
				{
					(item, i) => (
						<li>
							<TodoItem /> {`item: ${item} index: ${i()}`}
						</li>
					)
				}
			</For>
		</ul>
	)
}
