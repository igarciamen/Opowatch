import { CreateWatcherRequest } from './create-watcher-request';

export interface UpdateWatcherRequest extends CreateWatcherRequest {
  active: boolean;
}