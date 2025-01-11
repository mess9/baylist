import type { Component } from "solid-js";

import BuyList from "/widgets/BuyList";

import classes from "./Home.module.css";

const Home: Component = () => {
	return (
		<div class={classes["home-page"]}>
			<BuyList delimiter="bottom" />
		</div>
	);
};

export default Home;
