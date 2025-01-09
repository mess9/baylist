const cn = (...args: string[]): string => {
  return args
    .filter(Boolean)
    .map((arg) =>
      typeof arg === "object"
        ? Object.keys(arg).filter((key) => arg[key])
        : arg,
    )
    .flat()
    .join(" ");
};

export default cn;
