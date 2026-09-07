import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { DomSanitizer, SafeHtml } from '@angular/platform-browser';

import { FeedService } from '../../service/feed-service';
import { MatchedItem } from '../../model/matched-item';
import { AuthService } from '../../service/auth-service';

interface MonthOption {
  key: string;
  label: string;
}

@Component({
  selector: 'app-feed-component',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './feed-component.html',
  styleUrl: './feed-component.css'
})
export class FeedComponent implements OnInit {
  allItems: MatchedItem[] = [];
  filteredItems: MatchedItem[] = [];
  searchTerm = '';
  selectedMonth = '';
  selectedWatcher = '';
  monthOptions: MonthOption[] = [];
  watcherOptions: string[] = [];
  loading = false;
  errorMessage = '';

  private static readonly NEW_THRESHOLD_HOURS = 48;
  readonly skeletonPlaceholders = [1, 2, 3, 4];

  constructor(
    private feedService: FeedService,
    public auth: AuthService,
    private route: ActivatedRoute,
    private router: Router,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    const params = this.route.snapshot.queryParamMap;
    this.searchTerm = params.get('q') ?? '';
    this.selectedMonth = params.get('month') ?? '';
    this.selectedWatcher = params.get('watcher') ?? '';

    this.loadFeed();
  }

  loadFeed(): void {
    this.loading = true;
    this.errorMessage = '';
    this.feedService.getFeed().subscribe({
      next: (items) => {
        this.allItems = items;
        this.monthOptions = this.buildMonthOptions(items);
        this.watcherOptions = this.buildWatcherOptions(items);
        this.applyFilters();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load the feed';
        this.loading = false;
      }
    });
  }

  onFilterChange(): void {
    this.applyFilters();
    this.syncUrl();
  }

  get hasActiveFilters(): boolean {
    return !!this.searchTerm || !!this.selectedMonth || !!this.selectedWatcher;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.selectedMonth = '';
    this.selectedWatcher = '';
    this.applyFilters();
    this.syncUrl();
  }

  isNew(detectedAt: string): boolean {
    const detectedTime = new Date(detectedAt).getTime();
    const hoursSince = (Date.now() - detectedTime) / (1000 * 60 * 60);
    return hoursSince <= FeedComponent.NEW_THRESHOLD_HOURS;
  }

  highlightedTitle(title: string): SafeHtml {
    const term = this.searchTerm.trim();
    if (!term) {
      return this.sanitizer.bypassSecurityTrustHtml(this.escapeHtml(title));
    }

    const escapedTitle = this.escapeHtml(title);
    const escapedTerm = this.escapeHtml(term).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(`(${escapedTerm})`, 'ig');
    const highlighted = escapedTitle.replace(regex, '<mark>$1</mark>');

    return this.sanitizer.bypassSecurityTrustHtml(highlighted);
  }

  private escapeHtml(value: string): string {
    const div = document.createElement('div');
    div.textContent = value;
    return div.innerHTML;
  }

  private syncUrl(): void {
    this.router.navigate([], {
      relativeTo: this.route,
      queryParams: {
        q: this.searchTerm || null,
        month: this.selectedMonth || null,
        watcher: this.selectedWatcher || null
      },
      queryParamsHandling: 'merge',
      replaceUrl: true
    });
  }

  private applyFilters(): void {
    const term = this.searchTerm.trim().toLowerCase();

    this.filteredItems = this.allItems.filter((item) => {
      const matchesTerm = !term ||
        item.title.toLowerCase().includes(term) ||
        (item.organization ?? '').toLowerCase().includes(term) ||
        item.watcherName.toLowerCase().includes(term);

      const matchesMonth = !this.selectedMonth ||
        this.monthKey(item.detectedAt) === this.selectedMonth;

      const matchesWatcher = !this.selectedWatcher ||
        item.watcherName === this.selectedWatcher;

      return matchesTerm && matchesMonth && matchesWatcher;
    });
  }

  private buildMonthOptions(items: MatchedItem[]): MonthOption[] {
    const keys = new Set(items.map((item) => this.monthKey(item.detectedAt)));

    return Array.from(keys)
      .sort((a, b) => b.localeCompare(a))
      .map((key) => ({ key, label: this.monthLabel(key) }));
  }

  private buildWatcherOptions(items: MatchedItem[]): string[] {
    const names = new Set(items.map((item) => item.watcherName));
    return Array.from(names).sort((a, b) => a.localeCompare(b));
  }

  private monthKey(isoDateTime: string): string {
    return isoDateTime.substring(0, 7);
  }

  private monthLabel(key: string): string {
    const [year, month] = key.split('-').map(Number);
    const date = new Date(year, month - 1, 1);
    return new Intl.DateTimeFormat('en-US', { month: 'long', year: 'numeric' }).format(date);
  }
}