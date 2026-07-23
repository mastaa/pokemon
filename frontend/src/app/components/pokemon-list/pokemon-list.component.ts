import { Component, inject, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { PokemonService } from '../../services/pokemon.service';
import { PokemonDto, SortField, SortDir } from '../../models/pokemon.model';

@Component({
  selector: 'app-pokemon-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pokemon-list.component.html',
  styleUrl: './pokemon-list.component.scss'
})
export class PokemonListComponent implements OnInit {
  private readonly service = inject(PokemonService);
  private readonly router = inject(Router);

  readonly pageSize = 20;
  readonly offset = signal(0);
  readonly total = signal(0);
  readonly loading = signal(false);
  readonly error = signal<string | null>(null);

  readonly sortField = signal<SortField>('id');
  readonly sortDir = signal<SortDir>('asc');

  readonly rawPokemon = signal<PokemonDto[]>([]);

  readonly sorted = computed(() => {
    const field = this.sortField();
    const dir = this.sortDir();
    return [...this.rawPokemon()].sort((a, b) => {
      const av = a[field] ?? 0;
      const bv = b[field] ?? 0;
      const cmp = typeof av === 'string'
        ? (av as string).localeCompare(bv as string)
        : (av as number) - (bv as number);
      return dir === 'asc' ? cmp : -cmp;
    });
  });

  readonly currentPage = computed(() => Math.floor(this.offset() / this.pageSize) + 1);
  readonly totalPages = computed(() => Math.ceil(this.total() / this.pageSize));

  readonly columns: { field: SortField; label: string }[] = [
    { field: 'id', label: '#' },
    { field: 'name', label: 'Name' },
    { field: 'height', label: 'Height' },
    { field: 'weight', label: 'Weight' },
    { field: 'base_experience', label: 'Base XP' }
  ];

  ngOnInit() { this.load(); }

  load() {
    this.loading.set(true);
    this.error.set(null);
    this.service.listWithDetails(this.pageSize, this.offset()).subscribe({
      next: pokemon => {
        this.rawPokemon.set(pokemon);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Failed to load Pokémon. Please try again.');
        this.loading.set(false);
      }
    });
    // fetch total count separately for pagination
    this.service.list(1, 0).subscribe(l => this.total.set(l.count));
  }

  sort(field: SortField) {
    if (this.sortField() === field) {
      this.sortDir.update(d => d === 'asc' ? 'desc' : 'asc');
    } else {
      this.sortField.set(field);
      this.sortDir.set('asc');
    }
  }

  prevPage() {
    if (this.offset() > 0) {
      this.offset.update(o => Math.max(0, o - this.pageSize));
      this.load();
    }
  }

  nextPage() {
    if (this.offset() + this.pageSize < this.total()) {
      this.offset.update(o => o + this.pageSize);
      this.load();
    }
  }

  viewDetail(pokemon: PokemonDto) {
    this.router.navigate(['/pokemon', pokemon.name]);
  }

  typeColor(typeName: string): string {
    const colors: Record<string, string> = {
      fire: '#F08030', water: '#6890F0', grass: '#78C850', electric: '#F8D030',
      psychic: '#F85888', ice: '#98D8D8', dragon: '#7038F8', dark: '#705848',
      fairy: '#EE99AC', normal: '#A8A878', fighting: '#C03028', flying: '#A890F0',
      poison: '#A040A0', ground: '#E0C068', rock: '#B8A038', bug: '#A8B820',
      ghost: '#705898', steel: '#B8B8D0'
    };
    return colors[typeName] ?? '#68A090';
  }
}
