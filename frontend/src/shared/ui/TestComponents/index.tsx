import { BoardExample } from "/src/TmpExampleBoard/tmp-example-board";
import cn from "/shared/utils/cn";
import { SortableVerticalListExample } from "/src/TmpExampleBoard/tmp-exmp-vert";

export default function BaseTest() {
  return (
    <div>
      Шрифт PT sans
      <br />
      {cn("qwe", "qwe", "microtask", "custom_class-names")}
      <br />
      {/*{() => {
            throw new Error("Oh No");
            
          }}*/}
      <BoardExample />
      <SortableVerticalListExample />
    </div>
  )
}
