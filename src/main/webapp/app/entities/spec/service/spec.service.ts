import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ISpec, NewSpec } from '../spec.model';

export type PartialUpdateSpec = Partial<ISpec> & Pick<ISpec, 'id'>;

export type EntityResponseType = HttpResponse<ISpec>;
export type EntityArrayResponseType = HttpResponse<ISpec[]>;

@Injectable({ providedIn: 'root' })
export class SpecService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/specs');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(spec: NewSpec): Observable<EntityResponseType> {
    return this.http.post<ISpec>(this.resourceUrl, spec, { observe: 'response' });
  }

  update(spec: ISpec): Observable<EntityResponseType> {
    return this.http.put<ISpec>(`${this.resourceUrl}/${this.getSpecIdentifier(spec)}`, spec, { observe: 'response' });
  }

  partialUpdate(spec: PartialUpdateSpec): Observable<EntityResponseType> {
    return this.http.patch<ISpec>(`${this.resourceUrl}/${this.getSpecIdentifier(spec)}`, spec, { observe: 'response' });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ISpec>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ISpec[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getSpecIdentifier(spec: Pick<ISpec, 'id'>): number {
    return spec.id;
  }

  compareSpec(o1: Pick<ISpec, 'id'> | null, o2: Pick<ISpec, 'id'> | null): boolean {
    return o1 && o2 ? this.getSpecIdentifier(o1) === this.getSpecIdentifier(o2) : o1 === o2;
  }

  addSpecToCollectionIfMissing<Type extends Pick<ISpec, 'id'>>(
    specCollection: Type[],
    ...specsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const specs: Type[] = specsToCheck.filter(isPresent);
    if (specs.length > 0) {
      const specCollectionIdentifiers = specCollection.map(specItem => this.getSpecIdentifier(specItem)!);
      const specsToAdd = specs.filter(specItem => {
        const specIdentifier = this.getSpecIdentifier(specItem);
        if (specCollectionIdentifiers.includes(specIdentifier)) {
          return false;
        }
        specCollectionIdentifiers.push(specIdentifier);
        return true;
      });
      return [...specsToAdd, ...specCollection];
    }
    return specCollection;
  }
}
