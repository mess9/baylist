import { Meta, StoryObj } from "storybook-solidjs";

import BuyItem from "./BuyItem";

type Story = StoryObj<typeof meta>;

const meta = {
	title: "features/BuyItem",
	component: BuyItem,
} satisfies Meta;

export default meta;

export const Secondary: Story = {
  args: {

  },
};
