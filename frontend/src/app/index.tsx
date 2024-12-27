import cn from "/shared/utils/cn";
import Logo from "/shared/ui/Logo/Logo";
import Home from "/pages/Home";
import "./index.css";

export default function App() {
    return (
        <div>
            Шрифт PT sans
            <br />
            {cn("qwe", "qwe", "microtask", "custom_class-names")}
            <br />
            <Home />
        </div>
    );
}
