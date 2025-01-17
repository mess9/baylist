import type { Meta, StoryObj } from "storybook-solidjs";

import BuyList from "./BuyList";

type Story = StoryObj<typeof meta>;

const meta = {
  title: "widgets/BuyList",
  component: BuyList,
  tags: ["autodocs"],
  argTypes: {
    delimiter: {
      control: { type: "select" },
      options: ["top", "bottom"],
    },
  },
} satisfies Meta<typeof BuyList>;

export default meta;

export const List: Story = {
  args: {
    delimiter: "bottom",
  },
};
