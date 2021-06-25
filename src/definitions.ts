export interface RemindersPlugin {
  schedule(): void;
  cancel(): void;
  isAvailable(): { result: Boolean };
}
