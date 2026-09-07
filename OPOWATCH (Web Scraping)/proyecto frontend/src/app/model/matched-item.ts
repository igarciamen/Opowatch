export interface MatchedItem {
  id: number;
  watcherName: string;
  title: string;
  organization: string | null;
  publicationDate: string | null;
  sourceUrl: string;
  detectedAt: string;
}