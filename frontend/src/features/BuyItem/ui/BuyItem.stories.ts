import type { Meta, StoryObj } from "storybook-solidjs";

import BuyItem from "./BuyItem";

type Story = StoryObj<typeof meta>;

const meta = {
	title: "features/BuyItem",
	component: BuyItem,
	tags: ['autodocs'],
	argTypes: {
		countItemsOnVh: {
      control: {
        type: "select",
        options: ["8", "12", "16", "20", "24"], // Возможные значения
      },
      description: "Number of items visible per viewport height",
      defaultValue: "16",
    },
	}
} satisfies Meta;

export default meta;

export const Secondary: Story = {
  args: {

  },
};
