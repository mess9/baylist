import {
  berryRed,
  taupe,
  getColorById,
  getColorByName,
  getColorByKey,
  defaultColor,
} from "./colors";

describe("getColorById", () => {
  const colorTheories = [
    [0, defaultColor], // out of range, defaulted
    [30, berryRed],
    [49, taupe],
    [999, defaultColor], // out of range, defaulted
  ] as const;

  test.each(colorTheories)("id %p returns color %p", (id, expected) => {
    const color = getColorById(id);
    expect(color).toEqual(expected);
  });
});

describe("getColorByName", () => {
  const colorTheories = [
    ["Berry Red", berryRed],
    ["Taupe", taupe],
    ["berry_red", defaultColor], // does not exist, defaulted
    ["Some non existing color", defaultColor], // does not exist, defaulted
  ] as const;

  test.each(colorTheories)("name %p returns color %p", (name, expected) => {
    const color = getColorByName(name);
    expect(color).toEqual(expected);
  });
});

describe("getColorByKey", () => {
  const colorTheories = [
    ["berry_red", berryRed],
    ["taupe", taupe],
    ["Berry Red", defaultColor], // does not exist, defaulted
    ["Some non existing color", defaultColor], // does not exist, defaulted
  ] as const;

  test.each(colorTheories)("key %p returns color %p", (key, expected) => {
    const color = getColorByKey(key);
    expect(color).toEqual(expected);
  });
});
