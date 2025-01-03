import cn from "/shared/utils/cn";

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
    </div>
  )
}
