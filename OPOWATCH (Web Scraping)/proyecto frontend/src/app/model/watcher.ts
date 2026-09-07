export interface Watcher {
  id: number;
  name: string;
  sourceType: 'BOE_API' | 'SELENIUM' | 'JSON_API';
  targetUrl: string | null;
  listSelector: string | null;
  titleSelector: string | null;
  organizationSelector: string | null;
  dateSelector: string | null;
  linkSelector: string | null;
  itemsPath: string | null;
  titleField: string | null;
  linkField: string | null;
  dateField: string | null;
  organizationText: string | null;
  keywords: string;
  scrapeIntervalMinutes: number;
  active: boolean;
  createdAt: string;
  lastScrapedAt: string | null;
}