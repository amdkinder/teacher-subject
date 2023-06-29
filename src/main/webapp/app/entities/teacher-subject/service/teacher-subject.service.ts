import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { ITeacherSubject, NewTeacherSubject } from '../teacher-subject.model';

export type PartialUpdateTeacherSubject = Partial<ITeacherSubject> & Pick<ITeacherSubject, 'id'>;

export type EntityResponseType = HttpResponse<ITeacherSubject>;
export type EntityArrayResponseType = HttpResponse<ITeacherSubject[]>;

@Injectable({ providedIn: 'root' })
export class TeacherSubjectService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/teacher-subjects');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(teacherSubject: NewTeacherSubject): Observable<EntityResponseType> {
    return this.http.post<ITeacherSubject>(this.resourceUrl, teacherSubject, { observe: 'response' });
  }

  update(teacherSubject: ITeacherSubject): Observable<EntityResponseType> {
    return this.http.put<ITeacherSubject>(`${this.resourceUrl}/${this.getTeacherSubjectIdentifier(teacherSubject)}`, teacherSubject, {
      observe: 'response',
    });
  }

  partialUpdate(teacherSubject: PartialUpdateTeacherSubject): Observable<EntityResponseType> {
    return this.http.patch<ITeacherSubject>(`${this.resourceUrl}/${this.getTeacherSubjectIdentifier(teacherSubject)}`, teacherSubject, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<ITeacherSubject>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<ITeacherSubject[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  getTeacherSubjectIdentifier(teacherSubject: Pick<ITeacherSubject, 'id'>): number {
    return teacherSubject.id;
  }

  compareTeacherSubject(o1: Pick<ITeacherSubject, 'id'> | null, o2: Pick<ITeacherSubject, 'id'> | null): boolean {
    return o1 && o2 ? this.getTeacherSubjectIdentifier(o1) === this.getTeacherSubjectIdentifier(o2) : o1 === o2;
  }

  addTeacherSubjectToCollectionIfMissing<Type extends Pick<ITeacherSubject, 'id'>>(
    teacherSubjectCollection: Type[],
    ...teacherSubjectsToCheck: (Type | null | undefined)[]
  ): Type[] {
    const teacherSubjects: Type[] = teacherSubjectsToCheck.filter(isPresent);
    if (teacherSubjects.length > 0) {
      const teacherSubjectCollectionIdentifiers = teacherSubjectCollection.map(
        teacherSubjectItem => this.getTeacherSubjectIdentifier(teacherSubjectItem)!
      );
      const teacherSubjectsToAdd = teacherSubjects.filter(teacherSubjectItem => {
        const teacherSubjectIdentifier = this.getTeacherSubjectIdentifier(teacherSubjectItem);
        if (teacherSubjectCollectionIdentifiers.includes(teacherSubjectIdentifier)) {
          return false;
        }
        teacherSubjectCollectionIdentifiers.push(teacherSubjectIdentifier);
        return true;
      });
      return [...teacherSubjectsToAdd, ...teacherSubjectCollection];
    }
    return teacherSubjectCollection;
  }
}
