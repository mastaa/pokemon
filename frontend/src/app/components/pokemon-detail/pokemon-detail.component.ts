import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import { PokemonService } from '../../services/pokemon.service';
import { PokemonDto } from '../../models/pokemon.model';

@Component({
  selector: 'app-pokemon-detail',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pokemon-detail.component.html',
  styleUrl: './pokemon-detail.component.scss'
})
export class PokemonDetailComponent implements OnInit {
  private readonly service = inject(PokemonService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);

  readonly pokemon = signal<PokemonDto | null>(null);
  readonly loading = signal(true);
  readonly error = signal<string | null>(null);

  ngOnInit() {
    const nameOrId = this.route.snapshot.paramMap.get('nameOrId')!;
    this.service.get(nameOrId).subscribe({
      next: p => { this.pokemon.set(p); this.loading.set(false); },
      error: () => { this.error.set('Pokémon not found.'); this.loading.set(false); }
    });
  }

  goBack() { this.router.navigate(['/pokemon']); }

  statPercent(value: number): number { return Math.round((value / 255) * 100); }

  statColor(value: number): string {
    if (value >= 100) return '#48bb78';
    if (value >= 60) return '#4299e1';
    return '#f6ad55';
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
