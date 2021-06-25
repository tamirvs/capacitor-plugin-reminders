import { WebPlugin } from '@capacitor/core';
import type { RemindersPlugin } from './definitions';

export class RemindersWeb extends WebPlugin implements RemindersPlugin {
  schedule () {
    throw this.unavailable('Not available on web');
  }

  cancel () {
    throw this.unavailable('Not available on web');
  }

  isAvailable () {
    return {
      result: false
    }
  }
}
