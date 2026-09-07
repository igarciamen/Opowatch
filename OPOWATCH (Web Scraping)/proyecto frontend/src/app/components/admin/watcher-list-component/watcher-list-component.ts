import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

import { WatcherService } from '../../../service/watcher-service';
import { Watcher } from '../../../model/watcher';

@Component({
  selector: 'app-watcher-list-component',
  imports: [CommonModule, RouterLink],
  templateUrl: './watcher-list-component.html',
  styleUrl: './watcher-list-component.css'
})
export class WatcherListComponent implements OnInit {
  watchers: Watcher[] = [];
  loading = false;
  errorMessage = '';
  scrapingId: number | null = null;
  scrapeMessages: Record<number, string> = {};

  constructor(private watcherService: WatcherService) {}

  ngOnInit(): void {
    this.loadWatchers();
  }

  loadWatchers(): void {
    this.loading = true;
    this.errorMessage = '';
    this.watcherService.findAll().subscribe({
      next: (watchers) => {
        this.watchers = watchers;
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load watchers';
        this.loading = false;
      }
    });
  }

  scrapeNow(watcher: Watcher): void {
    this.scrapingId = watcher.id;
    delete this.scrapeMessages[watcher.id];

    this.watcherService.scrapeNow(watcher.id).subscribe({
      next: (result) => {
        this.scrapeMessages[watcher.id] = `${result.newItemsFound} new, ${result.updatedItemsFound} updated`;
        this.scrapingId = null;
      },
      error: () => {
        this.scrapeMessages[watcher.id] = 'Scrape failed';
        this.scrapingId = null;
      }
    });
  }

  deleteWatcher(watcher: Watcher): void {
    const confirmed = confirm(`Delete watcher "${watcher.name}"? This cannot be undone.`);
    if (!confirmed) {
      return;
    }

    this.watcherService.delete(watcher.id).subscribe({
      next: () => {
        this.watchers = this.watchers.filter(w => w.id !== watcher.id);
      },
      error: () => {
        this.errorMessage = 'Could not delete the watcher';
      }
    });
  }
}