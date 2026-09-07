import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { Watcher } from '../model/watcher';
import { ScrapeResult } from '../model/scrape-result';
import { MessageResponse } from '../model/message-response';
import { CreateWatcherRequest } from '../model/create-watcher-request';
import { UpdateWatcherRequest } from '../model/update-watcher-request';

@Injectable({
  providedIn: 'root'
})
export class WatcherService {
  private baseUrl = 'http://localhost:8082/api/watchers';

  constructor(private http: HttpClient) {}

  findAll(): Observable<Watcher[]> {
    return this.http.get<Watcher[]>(this.baseUrl);
  }

  findById(id: number): Observable<Watcher> {
    return this.http.get<Watcher>(`${this.baseUrl}/${id}`);
  }

  create(request: CreateWatcherRequest): Observable<Watcher> {
    return this.http.post<Watcher>(this.baseUrl, request);
  }

  update(id: number, request: UpdateWatcherRequest): Observable<Watcher> {
    return this.http.put<Watcher>(`${this.baseUrl}/${id}`, request);
  }

  delete(id: number): Observable<MessageResponse> {
    return this.http.delete<MessageResponse>(`${this.baseUrl}/${id}`);
  }

  scrapeNow(id: number): Observable<ScrapeResult> {
    return this.http.post<ScrapeResult>(`${this.baseUrl}/${id}/scrape`, {});
  }
}