const cn: (...args: any[]) => string = (...args: any[]): string => {
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
