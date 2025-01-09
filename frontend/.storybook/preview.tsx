import type { Preview } from "storybook-solidjs";

import { themes } from '@storybook/theming';
import { JSX } from "solid-js";

const preview: Preview = {
  // decorators: [
  //   (Story: () => JSX.Element): JSX.Element => (
  //     <div>
  //       <Story />
  //     </div>
  //   ),
  // ],
  // decorators: [
  //   (Story) => (
  //     <div style={{ margin: '3em' }}>
  //       <Story />
  //     </div>
  // )],
  parameters: {
    backgrounds: {
      values: [
        { name: "Dark", value: "#333" },
        { name: "Light", value: "#F7F9F2" },
        { name: "Maroon", value: "#400" },
      ],
      default: "Dark",
    },
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
    docs: {
      theme: themes.dark,
    },
    options: {
      storySort: {
        order: ["app", "pages", "widgets", "features", "entities", "shared", "Example"],
      },
    },
  },
};

// const Dec = (Story) => (
//       <div style={{ margin: '3em' }}>
//         <Story />
//       </div>
// )

// import './styles/global.css';

// export const globalTypes = {
//   theme: {
//     name: 'Theme',
//     description: 'Global theme for components',
//     defaultValue: 'default',
//     toolbar: {
//       icon: 'paintbrush',
//       items: ['default', 'dark'],
//     },
//   },
// };

// export const decorators = [
//   (Story, context) => {
//     const theme = context.globals.theme;
//     return (
//       <div data-theme={theme}>
//         <Story />
//       </div>
//     );
//   },
// ];

export default preview;
