import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ITeacherSubject } from '../teacher-subject.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../teacher-subject.test-samples';

import { TeacherSubjectService } from './teacher-subject.service';

const requireRestSample: ITeacherSubject = {
  ...sampleWithRequiredData,
};

describe('TeacherSubject Service', () => {
  let service: TeacherSubjectService;
  let httpMock: HttpTestingController;
  let expectedResult: ITeacherSubject | ITeacherSubject[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(TeacherSubjectService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should create a TeacherSubject', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const teacherSubject = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(teacherSubject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a TeacherSubject', () => {
      const teacherSubject = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(teacherSubject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a TeacherSubject', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of TeacherSubject', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a TeacherSubject', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addTeacherSubjectToCollectionIfMissing', () => {
      it('should add a TeacherSubject to an empty array', () => {
        const teacherSubject: ITeacherSubject = sampleWithRequiredData;
        expectedResult = service.addTeacherSubjectToCollectionIfMissing([], teacherSubject);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teacherSubject);
      });

      it('should not add a TeacherSubject to an array that contains it', () => {
        const teacherSubject: ITeacherSubject = sampleWithRequiredData;
        const teacherSubjectCollection: ITeacherSubject[] = [
          {
            ...teacherSubject,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addTeacherSubjectToCollectionIfMissing(teacherSubjectCollection, teacherSubject);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a TeacherSubject to an array that doesn't contain it", () => {
        const teacherSubject: ITeacherSubject = sampleWithRequiredData;
        const teacherSubjectCollection: ITeacherSubject[] = [sampleWithPartialData];
        expectedResult = service.addTeacherSubjectToCollectionIfMissing(teacherSubjectCollection, teacherSubject);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teacherSubject);
      });

      it('should add only unique TeacherSubject to an array', () => {
        const teacherSubjectArray: ITeacherSubject[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const teacherSubjectCollection: ITeacherSubject[] = [sampleWithRequiredData];
        expectedResult = service.addTeacherSubjectToCollectionIfMissing(teacherSubjectCollection, ...teacherSubjectArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const teacherSubject: ITeacherSubject = sampleWithRequiredData;
        const teacherSubject2: ITeacherSubject = sampleWithPartialData;
        expectedResult = service.addTeacherSubjectToCollectionIfMissing([], teacherSubject, teacherSubject2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(teacherSubject);
        expect(expectedResult).toContain(teacherSubject2);
      });

      it('should accept null and undefined values', () => {
        const teacherSubject: ITeacherSubject = sampleWithRequiredData;
        expectedResult = service.addTeacherSubjectToCollectionIfMissing([], null, teacherSubject, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(teacherSubject);
      });

      it('should return initial array if no TeacherSubject is added', () => {
        const teacherSubjectCollection: ITeacherSubject[] = [sampleWithRequiredData];
        expectedResult = service.addTeacherSubjectToCollectionIfMissing(teacherSubjectCollection, undefined, null);
        expect(expectedResult).toEqual(teacherSubjectCollection);
      });
    });

    describe('compareTeacherSubject', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareTeacherSubject(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareTeacherSubject(entity1, entity2);
        const compareResult2 = service.compareTeacherSubject(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareTeacherSubject(entity1, entity2);
        const compareResult2 = service.compareTeacherSubject(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareTeacherSubject(entity1, entity2);
        const compareResult2 = service.compareTeacherSubject(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
