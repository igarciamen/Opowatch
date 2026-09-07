import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';

import { UserService } from '../../service/user-service';
import { UserInfo } from '../../model/user-info';

@Component({
  selector: 'app-profile-component',
  imports: [CommonModule],
  templateUrl: './profile-component.html',
  styleUrl: './profile-component.css'
})
export class ProfileComponent implements OnInit {
  user: UserInfo | null = null;
  loading = false;
  saving = false;
  errorMessage = '';
  successMessage = '';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loading = true;
    this.userService.getMe().subscribe({
      next: (user) => {
        this.user = user;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load your profile';
        this.loading = false;
      }
    });
  }

  toggleNotifications(): void {
    if (!this.user) {
      return;
    }

    const newValue = !this.user.subscribedToNotifications;
    this.saving = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.userService.updateSubscription(newValue).subscribe({
      next: () => {
        this.user!.subscribedToNotifications = newValue;
        this.successMessage = newValue
          ? 'Notifications enabled. You will receive email alerts for new postings.'
          : 'Notifications disabled. You will not receive email alerts.';
        this.saving = false;
      },
      error: () => {
        this.errorMessage = 'Could not update your notification preference';
        this.saving = false;
      }
    });
  }
}