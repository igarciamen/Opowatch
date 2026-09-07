import { Routes } from '@angular/router';
import { SignupComponent } from './components/signup-component/signup-component';
import { LoginComponent } from './components/login-component/login-component';
import { AuthGuard } from './guards/auth-guard';
import { WatcherListComponent } from './components/admin/watcher-list-component/watcher-list-component';
import { WatcherFormComponent } from './components/admin/watcher-form-component/watcher-form-component';
import { FeedComponent } from './components/feed-component/feed-component';
import { ProfileComponent } from './components/profile-component/profile-component';

export const routes: Routes = [
  { path: '', component: FeedComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [AuthGuard] },
  {
    path: 'admin/watchers',
    component: WatcherListComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/watchers/new',
    component: WatcherFormComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
  {
    path: 'admin/watchers/:id/edit',
    component: WatcherFormComponent,
    canActivate: [AuthGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },
];