import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, switchMap, forkJoin } from 'rxjs';
import { PokemonDto, PokemonListDto } from '../models/pokemon.model';

@Injectable({ providedIn: 'root' })
export class PokemonService {
  private readonly http = inject(HttpClient);

  list(limit: number, offset: number): Observable<PokemonListDto> {
    const params = new HttpParams()
      .set('limit', limit)
      .set('offset', offset);
    return this.http.get<PokemonListDto>('/api/pokemon', { params });
  }

  get(nameOrId: string | number): Observable<PokemonDto> {
    return this.http.get<PokemonDto>(`/api/pokemon/${nameOrId}`);
  }

  listWithDetails(limit: number, offset: number): Observable<PokemonDto[]> {
    return this.list(limit, offset).pipe(
      switchMap(list =>
        forkJoin(list.results.map(r => this.get(r.name)))
      )
    );
  }
}
