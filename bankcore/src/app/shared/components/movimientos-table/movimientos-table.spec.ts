import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovimientosTable } from './movimientos-table';

describe('MovimientosTable', () => {
  let component: MovimientosTable;
  let fixture: ComponentFixture<MovimientosTable>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MovimientosTable],
    }).compileComponents();

    fixture = TestBed.createComponent(MovimientosTable);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
