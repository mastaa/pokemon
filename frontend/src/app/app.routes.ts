import { Routes } from '@angular/router';

export const routes: Routes = [
  { path: '', redirectTo: 'pokemon', pathMatch: 'full' },
  {
    path: 'pokemon',
    loadComponent: () =>
      import('./components/pokemon-list/pokemon-list.component').then(m => m.PokemonListComponent)
  },
  {
    path: 'pokemon/:nameOrId',
    loadComponent: () =>
      import('./components/pokemon-detail/pokemon-detail.component').then(m => m.PokemonDetailComponent)
  },
  { path: '**', redirectTo: 'pokemon' }
];
