export interface NamedResource {
  name: string;
  url: string;
}

export interface PokemonListDto {
  count: number;
  next: string | null;
  previous: string | null;
  results: NamedResource[];
}

export interface TypeSlot {
  slot: number;
  type: NamedResource;
}

export interface AbilitySlot {
  ability: NamedResource;
  is_hidden: boolean;
  slot: number;
}

export interface StatSlot {
  base_stat: number;
  effort: number;
  stat: NamedResource;
}

export interface Sprites {
  front_default: string;
}

export interface PokemonDto {
  id: number;
  name: string;
  height: number;
  weight: number;
  base_experience: number | null;
  types: TypeSlot[];
  abilities: AbilitySlot[];
  stats: StatSlot[];
  sprites: Sprites;
}

export type SortField = 'id' | 'name' | 'height' | 'weight' | 'base_experience';
export type SortDir = 'asc' | 'desc';
