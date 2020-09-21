export interface Event {
  id: string;
  description: string;
  duration: {
    seconds: number
  },
  start: {
    seconds: number
  }
}
