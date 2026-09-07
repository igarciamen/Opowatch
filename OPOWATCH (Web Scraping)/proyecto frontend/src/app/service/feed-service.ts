import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { MatchedItem } from '../model/matched-item';

@Injectable({
  providedIn: 'root'
})
export class FeedService {
  private url = 'http://localhost:8082/api/watchers/feed';

  constructor(private http: HttpClient) {}

  getFeed(): Observable<MatchedItem[]> {
    return this.http.get<MatchedItem[]>(this.url);
  }
}