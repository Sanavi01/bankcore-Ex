import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TarjetasList } from './tarjetas-list';

describe('TarjetasList', () => {
  let component: TarjetasList;
  let fixture: ComponentFixture<TarjetasList>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TarjetasList],
    }).compileComponents();

    fixture = TestBed.createComponent(TarjetasList);
    component = fixture.componentInstance;
    fixture.componentRef.setInput('tarjetas', []);
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
