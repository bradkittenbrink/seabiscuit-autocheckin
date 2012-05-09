package com.coffeeandpower.utils;

import android.graphics.Bitmap;

public class GraphicUtils
	{

		/**
		 * Resize profaile image if width or height > 512 px
		 * 
		 * @param image
		 * @return
		 */
		public static Bitmap resizeProfileImage (Bitmap image)
			{
				int ratio = 1;

				if (image.getHeight () > 512 || image.getWidth () > 512)
					{

						if (image.getWidth () < image.getHeight ())
							{
								ratio = image.getWidth () / 512;
							}
						else
							{
								ratio = image.getHeight () / 512;
							}

						Bitmap resizedImage = Bitmap.createScaledBitmap (image, image.getWidth () / ratio, image.getHeight () / ratio, false);

						// Be careful, it's recycled!!!
						image.recycle ();

						return resizedImage;
					}

				return image;
			}

	}
