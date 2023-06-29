import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ISpec } from '../spec.model';
import { sampleWithRequiredData, sampleWithNewData, sampleWithPartialData, sampleWithFullData } from '../spec.test-samples';

import { SpecService } from './spec.service';

const requireRestSample: ISpec = {
  ...sampleWithRequiredData,
};

describe('Spec Service', () => {
  let service: SpecService;
  let httpMock: HttpTestingController;
  let expectedResult: ISpec | ISpec[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(SpecService);
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

    it('should create a Spec', () => {
      // eslint-disable-next-line @typescript-eslint/no-unused-vars
      const spec = { ...sampleWithNewData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.create(spec).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Spec', () => {
      const spec = { ...sampleWithRequiredData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.update(spec).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Spec', () => {
      const patchObject = { ...sampleWithPartialData };
      const returnedFromService = { ...requireRestSample };
      const expected = { ...sampleWithRequiredData };

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Spec', () => {
      const returnedFromService = { ...requireRestSample };

      const expected = { ...sampleWithRequiredData };

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toMatchObject([expected]);
    });

    it('should delete a Spec', () => {
      const expected = true;

      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult).toBe(expected);
    });

    describe('addSpecToCollectionIfMissing', () => {
      it('should add a Spec to an empty array', () => {
        const spec: ISpec = sampleWithRequiredData;
        expectedResult = service.addSpecToCollectionIfMissing([], spec);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spec);
      });

      it('should not add a Spec to an array that contains it', () => {
        const spec: ISpec = sampleWithRequiredData;
        const specCollection: ISpec[] = [
          {
            ...spec,
          },
          sampleWithPartialData,
        ];
        expectedResult = service.addSpecToCollectionIfMissing(specCollection, spec);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Spec to an array that doesn't contain it", () => {
        const spec: ISpec = sampleWithRequiredData;
        const specCollection: ISpec[] = [sampleWithPartialData];
        expectedResult = service.addSpecToCollectionIfMissing(specCollection, spec);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spec);
      });

      it('should add only unique Spec to an array', () => {
        const specArray: ISpec[] = [sampleWithRequiredData, sampleWithPartialData, sampleWithFullData];
        const specCollection: ISpec[] = [sampleWithRequiredData];
        expectedResult = service.addSpecToCollectionIfMissing(specCollection, ...specArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const spec: ISpec = sampleWithRequiredData;
        const spec2: ISpec = sampleWithPartialData;
        expectedResult = service.addSpecToCollectionIfMissing([], spec, spec2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(spec);
        expect(expectedResult).toContain(spec2);
      });

      it('should accept null and undefined values', () => {
        const spec: ISpec = sampleWithRequiredData;
        expectedResult = service.addSpecToCollectionIfMissing([], null, spec, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(spec);
      });

      it('should return initial array if no Spec is added', () => {
        const specCollection: ISpec[] = [sampleWithRequiredData];
        expectedResult = service.addSpecToCollectionIfMissing(specCollection, undefined, null);
        expect(expectedResult).toEqual(specCollection);
      });
    });

    describe('compareSpec', () => {
      it('Should return true if both entities are null', () => {
        const entity1 = null;
        const entity2 = null;

        const compareResult = service.compareSpec(entity1, entity2);

        expect(compareResult).toEqual(true);
      });

      it('Should return false if one entity is null', () => {
        const entity1 = { id: 123 };
        const entity2 = null;

        const compareResult1 = service.compareSpec(entity1, entity2);
        const compareResult2 = service.compareSpec(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey differs', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 456 };

        const compareResult1 = service.compareSpec(entity1, entity2);
        const compareResult2 = service.compareSpec(entity2, entity1);

        expect(compareResult1).toEqual(false);
        expect(compareResult2).toEqual(false);
      });

      it('Should return false if primaryKey matches', () => {
        const entity1 = { id: 123 };
        const entity2 = { id: 123 };

        const compareResult1 = service.compareSpec(entity1, entity2);
        const compareResult2 = service.compareSpec(entity2, entity1);

        expect(compareResult1).toEqual(true);
        expect(compareResult2).toEqual(true);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
