package mpicbg.imglib.container.cell;

import mpicbg.imglib.Localizable;
import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.container.AbstractImgRandomAccess;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.container.basictypecontainer.array.ArrayDataAccess;
import mpicbg.imglib.type.NativeType;

/**
 * This {@link ImgRandomAccess} assumes that successive accesses fall
 * within different cells more often than not.
 * No checks are performed to determine whether we stay in the same cell.
 * Instead, the cell position is computed and set on every access.  
 */
public class CellRandomAccess< T extends NativeType< T >, A extends ArrayDataAccess< A > > extends AbstractImgRandomAccess< T > implements CellContainer.CellContainerSampler< T, A >
{
	protected final T type;
	
	protected final CellContainer< T, A > container;

	protected final RandomAccess< Cell< A > > cursorOnCells; // randomAccessOnCells;

	protected final int[] defaultCellDims;

	protected final long[] positionOfCurrentCell;
	protected final long[] positionInCell;
	
	protected int[] currentCellSteps;
	protected long[] currentCellMin;
	protected long[] currentCellMax;

	public CellRandomAccess( final CellContainer< T, A > container )
	{
		super( container );
		
		this.type = container.createLinkedType();
		this.container = container;
		this.cursorOnCells = container.cells.randomAccess();
		this.defaultCellDims = container.cellDims;
		
		this.positionOfCurrentCell = new long[ n ];
		this.positionInCell = new long[ n ];
		
		for ( int d = 0; d < n; ++d )
			position[ d ] = 0;
		
		container.splitGlobalPosition( position, positionOfCurrentCell, positionInCell );
		cursorOnCells.setPosition( positionOfCurrentCell );
		updatePosition();
	}

	@Override
	public Cell< A > getCell()
	{
		return cursorOnCells.get();
	}

	@Override
	public T get()
	{
		return type;
	}
	
	@Override
	public void fwd( int dim )
	{
		type.incIndex( currentCellSteps[ dim ] );
		if ( ++position[ dim ] > currentCellMax[ dim ] )
		{
			cursorOnCells.fwd( dim );			
			updatePosition();
		}
	}

	@Override
	public void bck( int dim )
	{
		type.decIndex( currentCellSteps[ dim ] );
		if ( --position[ dim ] < currentCellMin[ dim ] )
		{
			cursorOnCells.bck( dim );			
			updatePosition();
		}
	}

	@Override
	public void move( final long distance, final int dim )
	{
		type.incIndex( ( int ) distance * currentCellSteps[ dim ] );
		position[ dim ] += distance;
		if ( position[ dim ] < currentCellMin[ dim ] || position[ dim ] > currentCellMax[ dim ] )
		{
			cursorOnCells.setPosition( position[ dim ] / defaultCellDims[ dim ], dim );			
			updatePosition();
		}
	}

	@Override
	public void move( final Localizable localizable )
	{
		localizable.localize( tmp );
		move( tmp );
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( distance[ d ] != 0 )
			{
				type.incIndex( ( int ) distance[ d ] * currentCellSteps[ d ] );
				position[ d ] += distance[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}
					
					updatePosition();
				}
			}
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( distance[ d ] != 0 )
			{
				type.incIndex( ( int ) distance[ d ] * currentCellSteps[ d ] );
				position[ d ] += distance[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( distance[ d ] != 0 )
						{
							position[ d ] += distance[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}
					
					updatePosition();
				}
			}
		}
	}

	@Override
	public void setPosition( final long pos, final int dim )
	{
		type.incIndex( ( int ) ( pos - position[ dim ] ) * currentCellSteps[ dim ] );
		position[ dim ] = pos;
		if ( pos < currentCellMin[ dim ] || pos > currentCellMax[ dim ] )
		{
			cursorOnCells.setPosition( pos / defaultCellDims[ dim ], dim );			
			updatePosition();
		}
	}
	
	@Override
	public void setPosition( final int[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				type.incIndex( ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ] );
				position[ d ] = pos[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}
					
					updatePosition();
				}
			}
		}
	}

	@Override
	public void setPosition( final long[] pos )
	{
		for ( int d = 0; d < n; ++d )
		{
			if ( pos[ d ] != position[ d ] )
			{
				type.incIndex( ( int ) ( pos[ d ] - position[ d ] ) * currentCellSteps[ d ] );
				position[ d ] = pos[ d ];
				if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
				{
					cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );

					for ( ++d; d < n; ++d )
					{
						if ( pos[ d ] != position[ d ] )
						{
							position[ d ] = pos[ d ];
							if ( position[ d ] < currentCellMin[ d ] || position[ d ] > currentCellMax[ d ] )
							{
								cursorOnCells.setPosition( position[ d ] / defaultCellDims[ d ], d );
							}
						}
					}
					
					updatePosition();
				}
			}
		}
	}

	@Override
	public CellContainer< T, ? > getImg()
	{
		return container;
	}

	/**
	 * Update type to currentCellSteps, currentCellMin, and type after
	 * switching cells. This is called after cursorOnCells and position
	 * fields have been set. 
	 */
	private void updatePosition()
	{
		final Cell< A > cell = getCell();

		currentCellSteps = cell.steps;
		currentCellMin = cell.offset;			
		currentCellMax = cell.max;			

		for ( int d = 0; d < n; ++d )
			positionInCell[ d ] = position[ d ] - currentCellMin[ d ];

		type.updateIndex( cell.localPositionToIndex( positionInCell ) );
		type.updateContainer( this );
	}
}