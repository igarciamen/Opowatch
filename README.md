# OpoWatch

OpoWatch is a public sector job posting monitor. It watches multiple official sources (the Spanish State Gazette, town hall and provincial council websites, open data APIs), filters postings by keyword, detects new and updated postings, and notifies subscribed users by email. A public feed lets anyone browse and search detected postings without logging in.

Built as a learning project to practice web scraping, REST APIs, and microservices with Spring Boot and Angular.

## Demo



https://github.com/user-attachments/assets/a8745016-11dd-48fb-a23d-055b86b63279



## Architecture

Four independent Spring Boot microservices, no API gateway, communicating over plain REST with a shared JWT secret:

| Service | Port | Database | Responsibility |
|---|---|---|---|
| `users` | 8081 | `usersOpoWatch` | Authentication, registration, roles, subscription preference |
| `watchers` | 8082 | `watchersOpoWatch` | Watcher CRUD, scheduling, keyword filtering, new/updated detection, public feed |
| `scraper-engine` | 8083 | none (stateless) | Raw extraction: BOE API client, Selenium scraper, JSON API client, PDF text reader |
| `notifications` | 8084 | none (stateless) | Sends email alerts via SMTP |

The frontend is a single Angular application (`proyecto frontend`) that talks to all four services directly from the browser.

```
Angular frontend
      |
      +--> users (auth, profile, subscribers list)
      |
      +--> watchers (CRUD, feed, scrape now)
              |
              +--> scraper-engine (BOE / Selenium / JSON API / PDF text)
              |
              +--> notifications (email on new/updated postings)
```

## Tech stack

- **Backend**: Java 21+, Spring Boot 4.x, Spring Security (JWT resource server), Spring Data JPA, PostgreSQL, Selenium 4, Apache PDFBox, JsonPath (with Jackson provider), springdoc-openapi (Swagger UI on each service)
- **Frontend**: Angular 20, Bootstrap 5, standalone components, reactive forms

## Watcher types

Each watcher belongs to exactly one source type, chosen based on what the target site actually offers:

- **`BOE_API`** — calls the official BOE open data API directly. Also runs a deep PDF check: for postings whose title is too generic to classify (common with small town councils), it downloads and reads the linked PDF looking for keyword matches.
- **`SELENIUM`** — for sites with no API, using a headless Chrome browser and CSS selectors relative to a repeating list item (title, organization, date, link selectors, all configurable per watcher).
- **`JSON_API`** — for sites that expose their own open data API in JSON but with an arbitrary structure. Uses a JsonPath expression (`itemsPath`) to flatten the response, plus field names for title/link/date. The organization is a fixed text set by the admin, since these APIs rarely include it per item.

A watcher's `keywords` field (comma-separated) is fully configurable per watcher — the project is not hardcoded to IT postings, any topic can be tracked.

## Change detection

Every scrape run computes a SHA-256 hash of each matched posting (title + organization + date):

- **New URL** → saved as a new item.
- **Known URL, different hash** → the record is updated and counted as "updated".
- **Known URL, same hash** → nothing happens.

Both the manual "Scrape now" action and the automatic scheduler (checks every minute which watchers are due, based on each watcher's own interval) report `newItemsFound` and `updatedItemsFound` separately, and both trigger email notifications to subscribers.

## Frontend routes

| Route | Access | Purpose |
|---|---|---|
| `/` | Public | Feed of all detected postings, with search, month filter, and watcher filter |
| `/login` | Public | Login |
| `/signup` | Public | Registration (subscribes to notifications by default) |
| `/profile` | Any logged-in user | View account info, toggle email notifications |
| `/admin/watchers` | `ROLE_ADMIN` | List, edit, delete, and manually trigger watchers |
| `/admin/watchers/new`, `/admin/watchers/:id/edit` | `ROLE_ADMIN` | Create/edit a watcher, form adapts to the chosen source type |

## Key API endpoints

**users** (`:8081`)
- `POST /api/auth/signup`, `POST /api/auth/login`
- `GET /api/user/me`, `PATCH /api/user/me/subscription`
- `GET /api/user/subscribers` — internal only, requires `X-Internal-Api-Key` header

**watchers** (`:8082`)
- `GET /api/watchers`, `POST /api/watchers`, `PUT /api/watchers/{id}`, `DELETE /api/watchers/{id}` — admin only
- `POST /api/watchers/{id}/scrape` — runs a watcher immediately
- `GET /api/watchers/feed` — public, aggregated feed of all matched postings

**scraper-engine** (`:8083`), no authentication, internal use only
- `GET /api/scraper/boe?date=yyyy-MM-dd`
- `POST /api/scraper/selenium`
- `POST /api/scraper/json`
- `GET /api/scraper/document-text?url=...`

**notifications** (`:8084`), no authentication, internal use only
- `POST /api/notifications/send`

Full interactive documentation is available on each service's Swagger UI at `/swagger-ui.html`.

## Running locally

1. PostgreSQL running locally, with `usersOpoWatch` and `watchersOpoWatch` databases created.
2. Set `internal.api-key` to the same value in both `users` and `watchers` application properties.
3. Set SMTP credentials (Gmail app password) in `notifications` application properties.
4. Start the four Spring Boot services (`users`, `watchers`, `scraper-engine`, `notifications`), in any order — `watchers` retries scraper-engine and users calls gracefully if they are not yet up.
5. `cd` into `proyecto frontend` and run `ng serve`.
6. Open `http://localhost:4200`.

An initial `ROLE_ADMIN` user is seeded automatically on first startup of `users` via a `DataLoader`.

## Design notes and limitations worth knowing

- **Sources are only added after checking `robots.txt`.** Several promising sources (Boletín Oficial de Aragón, Diputación de Zaragoza, the state's internal employment search tool) were deliberately left out because their `robots.txt` disallows automated access, even where an official open dataset existed under a different, blocked hosting path.
- **Deduplication is per source URL, not per content.** Sources where the same URL always shows different content (e.g. a live clock) are not a good fit for a `SELENIUM` watcher as designed; they would need a value-based (not URL-based) change detection strategy.
- **BOE deep PDF check has a per-run cap** (`watchers.deep-check-max-per-run`) to avoid downloading too many PDFs in a single scheduled run.
- **`RSS_FEED` was considered but not implemented** — none of the sources investigated (Huesca, Teruel, their provincial councils, EU Careers) were confirmed to expose a dedicated employment RSS feed. Left as a documented idea for a future source that does.
