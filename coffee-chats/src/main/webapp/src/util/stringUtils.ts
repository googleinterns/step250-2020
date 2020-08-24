export const capitaliseEachWord = (input: string) => {
  return input.split(' ')
    .map((word) => word.charAt(0).toUpperCase() + word.substring(1))
    .join(' ');
}