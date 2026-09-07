import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { WatcherService } from '../../../service/watcher-service';
import { CreateWatcherRequest } from '../../../model/create-watcher-request';
import { UpdateWatcherRequest } from '../../../model/update-watcher-request';

@Component({
  selector: 'app-watcher-form-component',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './watcher-form-component.html',
  styleUrl: './watcher-form-component.css'
})
export class WatcherFormComponent implements OnInit {
  form: FormGroup;
  isEditMode = false;
  watcherId: number | null = null;
  loading = false;
  saving = false;
  errorMessage = '';

  private static readonly REQUIRED_FIELDS_BY_SOURCE: Record<string, string[]> = {
    SELENIUM: ['targetUrl', 'listSelector', 'titleSelector', 'linkSelector'],
    JSON_API: ['targetUrl', 'itemsPath', 'titleField', 'linkField']
  };

  private static readonly ALL_CONDITIONAL_FIELDS = [
    'targetUrl', 'listSelector', 'titleSelector', 'linkSelector',
    'itemsPath', 'titleField', 'linkField'
  ];

  constructor(
    private fb: FormBuilder,
    private watcherService: WatcherService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      name: ['', Validators.required],
      sourceType: ['BOE_API', Validators.required],
      targetUrl: [''],
      listSelector: [''],
      titleSelector: [''],
      organizationSelector: [''],
      dateSelector: [''],
      linkSelector: [''],
      itemsPath: [''],
      titleField: [''],
      linkField: [''],
      dateField: [''],
      organizationText: [''],
      keywords: ['', Validators.required],
      scrapeIntervalMinutes: [60, [Validators.required, Validators.min(1)]],
      active: [true]
    });
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.watcherId = Number(idParam);
      this.loadWatcher(this.watcherId);
    }

    this.form.get('sourceType')!.valueChanges.subscribe(() => this.updateSourceValidators());
    this.updateSourceValidators();
  }

  get isSelenium(): boolean {
    return this.form.get('sourceType')!.value === 'SELENIUM';
  }

  get isJsonApi(): boolean {
    return this.form.get('sourceType')!.value === 'JSON_API';
  }

  get name() { return this.form.get('name')!; }
  get sourceType() { return this.form.get('sourceType')!; }
  get targetUrl() { return this.form.get('targetUrl')!; }
  get listSelector() { return this.form.get('listSelector')!; }
  get titleSelector() { return this.form.get('titleSelector')!; }
  get linkSelector() { return this.form.get('linkSelector')!; }
  get itemsPath() { return this.form.get('itemsPath')!; }
  get titleField() { return this.form.get('titleField')!; }
  get linkField() { return this.form.get('linkField')!; }
  get keywords() { return this.form.get('keywords')!; }
  get scrapeIntervalMinutes() { return this.form.get('scrapeIntervalMinutes')!; }

  onSubmit(): void {
    this.errorMessage = '';
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving = true;
    const value = this.form.value;
    const selenium = value.sourceType === 'SELENIUM';
    const jsonApi = value.sourceType === 'JSON_API';

    const basePayload = {
      name: value.name,
      sourceType: value.sourceType,
      targetUrl: (selenium || jsonApi) ? value.targetUrl : null,
      listSelector: selenium ? value.listSelector : null,
      titleSelector: selenium ? value.titleSelector : null,
      organizationSelector: selenium ? (value.organizationSelector || null) : null,
      dateSelector: selenium ? (value.dateSelector || null) : null,
      linkSelector: selenium ? value.linkSelector : null,
      itemsPath: jsonApi ? value.itemsPath : null,
      titleField: jsonApi ? value.titleField : null,
      linkField: jsonApi ? value.linkField : null,
      dateField: jsonApi ? (value.dateField || null) : null,
      organizationText: jsonApi ? (value.organizationText || null) : null,
      keywords: value.keywords,
      scrapeIntervalMinutes: value.scrapeIntervalMinutes
    };

    if (this.isEditMode && this.watcherId !== null) {
      const request: UpdateWatcherRequest = { ...basePayload, active: value.active };
      this.watcherService.update(this.watcherId, request).subscribe({
        next: () => this.router.navigateByUrl('/admin/watchers'),
        error: (err) => this.handleError(err)
      });
    } else {
      const request: CreateWatcherRequest = basePayload;
      this.watcherService.create(request).subscribe({
        next: () => this.router.navigateByUrl('/admin/watchers'),
        error: (err) => this.handleError(err)
      });
    }
  }

  private loadWatcher(id: number): void {
    this.loading = true;
    this.watcherService.findById(id).subscribe({
      next: (watcher) => {
        this.form.patchValue({
          name: watcher.name,
          sourceType: watcher.sourceType,
          targetUrl: watcher.targetUrl ?? '',
          listSelector: watcher.listSelector ?? '',
          titleSelector: watcher.titleSelector ?? '',
          organizationSelector: watcher.organizationSelector ?? '',
          dateSelector: watcher.dateSelector ?? '',
          linkSelector: watcher.linkSelector ?? '',
          itemsPath: watcher.itemsPath ?? '',
          titleField: watcher.titleField ?? '',
          linkField: watcher.linkField ?? '',
          dateField: watcher.dateField ?? '',
          organizationText: watcher.organizationText ?? '',
          keywords: watcher.keywords ?? '',
          scrapeIntervalMinutes: watcher.scrapeIntervalMinutes,
          active: watcher.active
        });
        this.updateSourceValidators();
        this.loading = false;
      },
      error: () => {
        this.errorMessage = 'Could not load the watcher';
        this.loading = false;
      }
    });
  }

  private updateSourceValidators(): void {
    const requiredFields = WatcherFormComponent.REQUIRED_FIELDS_BY_SOURCE[this.sourceType.value] || [];

    for (const field of WatcherFormComponent.ALL_CONDITIONAL_FIELDS) {
      const control = this.form.get(field)!;
      if (requiredFields.includes(field)) {
        control.setValidators([Validators.required]);
      } else {
        control.clearValidators();
      }
      control.updateValueAndValidity({ emitEvent: false });
    }
  }

  private handleError(err: any): void {
    this.saving = false;
    this.errorMessage = err?.error?.message || 'Could not save the watcher';
  }
}