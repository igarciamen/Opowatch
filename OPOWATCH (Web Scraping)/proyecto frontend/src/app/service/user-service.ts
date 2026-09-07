import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { UserInfo } from '../model/user-info';
import { UpdateSubscriptionRequest } from '../model/update-subscription-request';

@Injectable({
  providedIn: 'root'
})
export class UserService {
  private url = 'http://localhost:8081/api/user';

  constructor(private http: HttpClient) {}

  getMe(): Observable<UserInfo> {
    return this.http.get<UserInfo>(`${this.url}/me`);
  }

  updateSubscription(subscribed: boolean): Observable<{ message: string }> {
    const request: UpdateSubscriptionRequest = { subscribed };
    return this.http.patch<{ message: string }>(`${this.url}/me/subscription`, request);
  }
}